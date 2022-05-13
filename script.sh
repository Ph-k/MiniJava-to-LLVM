#!/bin/bash

RED="\e[31m"
GREEN="\e[32m"
YELLOW="\e[33m"
ENDCOLOR="\e[0m"

# When type checking fails the output MUST contain this string
WrongString="TypeCheckingException"

# When no type errors where found, the output MUST contain this string
CorrectString="No type erros for program"

if [[ $1 != "--no-type-check" &&  $2 != "--no-type-check" ]];
then
    pass=0
    fail=0
    check_manualy=0
    echo -e "Checking for program validity:"
    for input in ./../inputs/smar/* ./../inputs/smar/*/*; do
        if [[ -f ${input} ]]
        then
            output=$(java Main ${input} 2>&1)
            if [[ "$output" == *$WrongString* ]];
            then
                if [[ "$input" == *"error"* ]];
                then
                    echo -e "${input} was not accepted. ${GREEN}Pass${ENDCOLOR}"
                    pass=$((pass+1))
                else
                    echo -e "${input} was not accepted. ${RED}FAIL!!!!!${ENDCOLOR}"
                    fail=$((fail+1))

                fi
            elif [[ "$output" == *$CorrectString* ]];
            then
                if [[ "$input" == *"error"* ]];
                then
                    echo -e "${input} was accepted. ${RED}FAIL!!!!!${ENDCOLOR}"
                    fail=$((fail+1))

                else
                    echo -e "${input} was accepted. ${GREEN}Pass${ENDCOLOR}"
                    pass=$((pass+1))
                fi
            else
                echo -e "Unknown error for ${input}. ${YELLOW}Check manually${ENDCOLOR}"
                check_manualy=$((check_manualy+1))
            fi
        fi
    done
    echo -e "Input validity results: $pass ${GREEN}Passed${ENDCOLOR}, $fail ${RED}Failed${ENDCOLOR}, $check_manualy ${YELLOW}To be checked manually${ENDCOLOR}"
fi


if [[ $1 != "--no-offset-check" &&  $2 != "--no-offset-check" ]];
then
    pass=0
    fail=0
    echo -e "\nChecking for program offests:"
    for input in ./../inputs/smar/* ./../inputs/smar/*/*; do
        if [[ -f ${input} && "$input" != *"error"* ]];
        then
            # use the tail -n +x, where x is the number of the first lines of code you want the script to ignore
            output=$(java Main ${input} 2>&1 | tail -n +3)
            basename="$(basename -- $input)"
            basename=${basename%".java"}.txt

            offests=$(cat ../inputs/offset-examples/$basename)
            DIFF=$(diff -B <(echo "$offests") <(echo "$output"))
            if [[ $DIFF == "" ]];
            then
                echo -e "${input} ${GREEN}Pass${ENDCOLOR}"
                pass=$((pass+1))
            else
                echo -e "${input} ${RED}FAIL!!${ENDCOLOR}"
                diff -B <(echo "$offests") <(echo "$output")
                fail=$((fail+1))
            fi
        fi
    done
    echo -e "Offset results: $pass ${GREEN}Passed${ENDCOLOR}, $fail ${RED}Failed${ENDCOLOR}"
fi
