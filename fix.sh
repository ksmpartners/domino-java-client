#!/usr/bin/env bash

for f in ./src/conf/*.json; do
    sed 's/"format": "epoch"/"format": "int64"/g' -i "$f"
    sed 's/"parameters": null/"parameters": []/g' -i "$f"
done

for f in ./src/conf/*.json; do
    jq -S . "${f}" > "${f}.sorted"
    mv "${f}.sorted" "${f}"
done