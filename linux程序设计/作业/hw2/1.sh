#!/bin/sh
clear
select item in Continue Finish
do
case "$item" in
    Continue) ;;
    Finish) break ;;
    *) echo "Wrong choice! Please select again!" ;;
esac 
done