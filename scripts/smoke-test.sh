#!/usr/bin/env bash
# Comprobación de punta a punta contra el sistema levantado (ver README, "Arrancar"). Todo pasa por el gateway,
# salvo la consulta directa al Config Server. Solo necesita bash y curl. Sale con código 1 si algo falla.
#   scripts/smoke-test.sh              con los cinco procesos arriba
#   scripts/smoke-test.sh auth-caido   después de parar auth-service: fallback y 503
GW=${GATEWAY_URL:-http://localhost:8080}
CONFIG=${CONFIG_SERVER_URL:-http://localhost:8888}
TMP=$(mktemp -d); trap 'rm -rf "$TMP"' EXIT
PASS=0; FAIL=0
J=(-H 'Content-Type: application/json')

req() {   # req METODO PATH [TOKEN] [JSON] -> deja CODE y BODY
  local m=$1 p=$2 t=$3 d=$4
  local args=(-s -o $TMP/body -w '%{http_code}' -X "$m" "$GW$p")
  [ -n "$t" ] && args+=(-H "Authorization: Bearer $t")
  [ -n "$d" ] && args+=("${J[@]}" -d "$d")
  CODE=$(curl "${args[@]}"); BODY=$(cat $TMP/body)
}
check() { # check DESCRIPCION ESPERADO [texto que debe contener el cuerpo]
  if [ "$CODE" = "$2" ] && { [ -z "$3" ] || grep -q -- "$3" <<<"$BODY"; }; then
    PASS=$((PASS+1)); printf '  ok   %-70s %s\n' "$1" "$CODE"
  else
    FAIL=$((FAIL+1)); printf '  FAIL %-70s %s (esperado %s%s)\n        %s\n' "$1" "$CODE" "$2" "${3:+ con $3}" "${BODY:0:200}"
  fi
}
token() { sed -E 's/.*"token":"([^"]+)".*/\1/' <<<"$BODY"; }
section() { printf '\n%s\n' "$1"; }

if [ "$1" != "auth-caido" ]; then
  section "Config Server"
  CODE=$(curl -s -o $TMP/body -w '%{http_code}' $CONFIG/customer-service/dev); BODY=$(cat $TMP/body)
  check "config-server sirve customer-service/dev con la URL de auth" 200 '"minibank.services.auth-url"'

  section "Registro y login por el gateway"
  U="eva$RANDOM"; DOC="2$(printf '%07d' $RANDOM)0"
  req POST /auth/register "" "{\"username\":\"$U\",\"password\":\"Secret123\",\"documentType\":\"DNI\",\"documentNumber\":\"$DOC\",\"firstNames\":\"Eva\",\"lastNames\":\"Rojas\",\"email\":\"$U@mail.com\",\"phone\":\"999000111\"}"
  check "POST /auth/register crea cliente y usuario y devuelve token" 201 '"token"'
  EVA=$(token)
  req POST /auth/register "" "{\"username\":\"$U\",\"password\":\"Secret123\",\"documentType\":\"DNI\",\"documentNumber\":\"30000001\",\"firstNames\":\"Eva\",\"lastNames\":\"Rojas\",\"email\":\"x$U@mail.com\"}"
  check "registro con usuario repetido" 409
  req POST /auth/register "" "{\"username\":\"otro$U\",\"password\":\"Secret123\",\"documentType\":\"DNI\",\"documentNumber\":\"$DOC\",\"firstNames\":\"Eva\",\"lastNames\":\"Rojas\",\"email\":\"y$U@mail.com\"}"
  check "registro con documento repetido (409 de customer-service)" 409
  req POST /auth/login "" "{\"username\":\"$U\",\"password\":\"Secret123\"}"
  check "login del usuario recién registrado" 200 '"token"'
  req POST /auth/login "" '{"username":"demo","password":"demo123"}'
  check "login demo" 200 '"token"'
  DEMO=$(token)
  req POST /auth/login "" '{"username":"admin","password":"admin123"}'
  check "login admin" 200 '"token"'
  ADMIN=$(token)
  req POST /auth/login "" '{"username":"demo","password":"mala"}'
  check "login con contraseña incorrecta" 401 '"status":401'
  req POST /auth/login "" '{"username":"pedro","password":"pedro123"}'
  check "login de usuario bloqueado" 403 '"status":403'

  section "Errores 401 y 403"
  req GET /favorites
  check "sin token: el gateway responde 401 (ErrorResponse)" 401 '"status":401'
  req GET /favorites "token-falso"
  check "token inválido: el servicio responde 401 (ErrorResponse)" 401 '"status":401'
  req GET /admin/customers "$DEMO"
  check "cliente en /admin: 403 (ErrorResponse)" 403 '"status":403'
  req GET /internal/customers "$DEMO"
  check "/internal no está expuesto en el gateway" 404

  section "Rutas de admin"
  req GET /admin/customers "$ADMIN"
  check "admin lista los clientes (incluye el recién registrado)" 200 "$U@mail.com"

  section "CRUD de favoritos solo del dueño (y Feign a auth-service)"
  req POST /favorites "$DEMO" "{\"alias\":\"Primo$U\",\"accountNumber\":\"19100$RANDOM\",\"bank\":\"BBVA\",\"holder\":\"Juan Diaz\"}"
  check "crear favorito: auth responde y queda verificado" 201 '"ownerVerified":true'
  FID=$(sed -E 's/.*"id":([0-9]+).*/\1/' <<<"$BODY")
  req GET "/favorites/$FID" "$DEMO";                check "leer el propio" 200 "Primo$U"
  req PUT "/favorites/$FID" "$DEMO" "{\"alias\":\"Primo2$U\",\"accountNumber\":\"19100$RANDOM\",\"bank\":\"BBVA\",\"holder\":\"Juan Diaz\"}"
  check "modificar el propio" 200 "Primo2$U"
  req GET "/favorites/$FID" "$ADMIN";               check "otro cliente no lo ve (404)" 404
  req PUT "/favorites/$FID" "$ADMIN" '{"alias":"X","accountNumber":"1","bank":"B","holder":"H"}'
  check "otro cliente no lo modifica (404)" 404
  req DELETE "/favorites/$FID" "$ADMIN";            check "otro cliente no lo borra (404)" 404
  req DELETE "/favorites/$FID" "$DEMO";             check "borrar el propio" 204
  req GET "/favorites/$FID" "$DEMO";                check "ya no existe" 404
  req POST /favorites "$EVA" "{\"alias\":\"Mama\",\"accountNumber\":\"19111$RANDOM\",\"bank\":\"BCP\",\"holder\":\"Ana Rojas\"}"
  check "el usuario registrado crea su primer favorito" 201 '"ownerVerified":true'

  section "Cuentas por el gateway"
  req GET "/movements?accountId=1" "$DEMO";         check "movimientos de una cuenta propia" 200 'DECREASE'
  req POST /transfers "$DEMO" '{"sourceAccountId":3,"amount":10,"destinationCci":"00219100000000000001","destinationBank":"BCP","destinationHolder":"Ana"}'
  check "transferir desde una cuenta ajena (404)" 404
  printf '%s\n%s\n' "$DEMO" "$U" > ${TMPDIR:-/tmp}/minibank-smoke-tokens
else
  DEMO=$(sed -n 1p ${TMPDIR:-/tmp}/minibank-smoke-tokens)
  section "auth-service caído"
  req POST /auth/login "" '{"username":"demo","password":"demo123"}'
  check "login con auth caído: el gateway responde 503" 503 '"status":503'
  req POST /favorites "$DEMO" "{\"alias\":\"SinAuth$RANDOM\",\"accountNumber\":\"19122$RANDOM\",\"bank\":\"BBVA\",\"holder\":\"Juan Diaz\"}"
  check "crear favorito con auth caído: fallback, aceptado sin verificar" 201 '"ownerVerified":false'
  req GET /favorites "$DEMO"
  check "las lecturas no dependen de auth-service" 200 'SinAuth'
fi
printf '\nResultado: %s ok, %s fallos\n' "$PASS" "$FAIL"
[ "$FAIL" -eq 0 ]
