#!/bin/bash
set -uo pipefail
IFS=$'\n\t'

YELLOW='\033[0;33m'
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

SYSTEMURL=${1:-}
if [[ -z "$SYSTEMURL" ]]; then
    echo "usage: $0 SYSTEM_URL"
    exit 1
fi


INPUT='["+1983248", "001382355", "+147 8192", "+4439877"]'

REQUEST_RESULT=`curl -s --max-time 5 -d "$INPUT" "$SYSTEMURL" -H "Content-Type: application/json" -H "Accept: application/json"`

retVal=$?
if [ $retVal -ne 0 ]; then
    printf "${RED}FAIL${NC} the API could not be reached, returned an error, or did not reply within 5 seconds\n"
    exit $retVal
fi

ACTUAL=`echo ${REQUEST_RESULT} | jq -c -S .`

EXPECTED=`echo '{"1":{"Clothing":1,"Technology":2},"44":{"Banking":1}}' | jq -c -S .`

if diff <(echo "$EXPECTED") <(echo "$ACTUAL"); then
    printf "${GREEN}SUCCESS${NC} the API complies with the spec\n"
else
    printf "${RED}FAIL${NC} the API does not comply with the spec, see bellow\n"
    printf "${YELLOW}expected:${NC}\n"
    printf "$EXPECTED"
    printf "\n"
    printf "${YELLOW}got:${NC}\n"
    printf "$ACTUAL"
    printf "\n"
fi
