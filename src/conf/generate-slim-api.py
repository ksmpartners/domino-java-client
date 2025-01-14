#!/usr/bin/env python3
## Generates an OpenAPI (swagger) spec from an existing spec based on a list
## of endpoints by which to filter the endpoints and components that are
## included in the resultant spec.
##
## This script has two environment variables for the filenames that are inputs
## to the resultant spec:
##  
##  - DOMINO_INTERNAL_SPEC (defaults to 'src/conf/domino-openapi.json'): the
##                         source "Internal" OpenAPI spec from Domino for their
##                         v4 API endpoints
##  - INTERNAL_SPEC_USAGE_CSV (defaults to 'src/conf/domino-internal-api-usage.csv'):
##                            CSV of endpoints to extract from source spec, as
##                            well as any required schemas.
##
## The resultant OpenAPI spec will be output to stdout, to be redirected or
## examined as needed.
##
## For example:
##
## This updates the slim-api.json file in place using current versions of specs
##
## $ ./src/conf/generate-slim-api.py > ./src/conf/slim-api.json
##
## Output to stdout instead to view spec in terminal:
##
## $ ./src/conf/generate-slim-api.py
## < {
## <   "components": {
## <     ...
##
## Select a different CSV for get a different set of components and paths:
##
## $ INTERNAL_SPEC_USAGE_CSV=./domino-subset-specs.csv ./src/conf/generate-slim-api.py
import csv
import json
import os
import re

TOP_LEVEL_SPEC_KEYS_FOR_COPY = ['openapi', 'info', 'security', 'servers', 'tags']

RE_COMPONENT_RESPONSES_PATTERN = r'"#/components/responses/(\S+)"'
RE_COMPONENT_SCHEMAS_PATTERN = r'"#/components/schemas/(\S+)"'
RE_COMPONENT_RESPONSES = re.compile(RE_COMPONENT_RESPONSES_PATTERN)
RE_COMPONENT_SCHEMAS = re.compile(RE_COMPONENT_SCHEMAS_PATTERN)

DOMINO_INTERNAL_SPEC = os.environ.get('DOMINO_INTERNAL_SPEC', 'src/conf/domino-openapi.json')
INTERNAL_SPEC_USAGE_CSV = os.environ.get('INTERNAL_SPEC_USAGE_CSV', 'src/conf/domino-internal-api-usage.csv')

endpoint_methods = {}
internal_spec_dict = {}

# Read usage CSV
with open(INTERNAL_SPEC_USAGE_CSV, newline = '') as spec_usage_file:
    spec_usage_reader = csv.DictReader(spec_usage_file)

    for spec in spec_usage_reader:
        method = spec['HTTP Method']
        endpoint = spec['HTTP Endpoint']

        if endpoint in endpoint_methods:
            endpoint_methods[endpoint].append(method)
        else:
            endpoint_methods[endpoint] = [method]

# Read internal spec
with open(DOMINO_INTERNAL_SPEC, newline = '') as internal_spec_file:
    internal_spec_dict = json.load(internal_spec_file)
    
# Prepare output spec - copy top-level values that don't need to be filtered
output_spec_dict = {}

for key in TOP_LEVEL_SPEC_KEYS_FOR_COPY:
    output_spec_dict[key] = internal_spec_dict[key]

# Pull and filter all endpoints from spec
output_spec_paths = {}

component_responses_set = set()
component_schemas_set = set()

# Iterate over endpoints
for endpoint, methods in endpoint_methods.items():
    assert endpoint in internal_spec_dict['paths']
    path_dict = internal_spec_dict['paths'][endpoint]
    new_path_dict = {}

    for method in methods:
        assert method in path_dict
        new_path_dict[method] = path_dict[method]

        # Load all references to components to sets to dereference in the next step
        responses_match = RE_COMPONENT_RESPONSES.findall(json.dumps(path_dict[method]))
        if responses_match:
            component_responses_set.update(responses_match)
        schemas_match = RE_COMPONENT_SCHEMAS.findall(json.dumps(path_dict[method]))
        if schemas_match:
            component_schemas_set.update(schemas_match)

    output_spec_paths[endpoint] = new_path_dict

output_spec_dict['paths'] = output_spec_paths

# Pull and filter all component responses from spec
#
# Incidentally, does not seem like there are any references to component
# responses within the entire components spec, so we do not need to recurse
component_responses_spec_dict = {}
responses_dict = internal_spec_dict['components']['responses']

for response in component_responses_set:
    assert response in responses_dict
    component_responses_spec_dict[response] = responses_dict[response]

    # Search for any additional schemas marked in spec
    schemas_match = RE_COMPONENT_SCHEMAS.findall(json.dumps(responses_dict[response]))
    if schemas_match:
        component_schemas_set.update(schemas_match)

# Recurse to pull and filter all component responses from spec
component_schemas_spec_dict = {}
schemas_dict = internal_spec_dict['components']['schemas']


# Recurse internal spec tree for all schemas required by endpoints, and their responses and schemas
def walk_schema_tree(schema_name):
    # Prevent circular walks - only parse schema if not already set on dict
    if schema_name not in component_schemas_spec_dict:
        assert schema_name in schemas_dict
        component_schemas_spec_dict[schema_name] = schemas_dict[schema_name]

        # Search for any additional schemas marked in spec
        schemas_match = RE_COMPONENT_SCHEMAS.findall(json.dumps(schemas_dict[schema_name]))
        if schemas_match:
            for schema in schemas_match:
                walk_schema_tree(schema)


for schema in component_schemas_set:
    walk_schema_tree(schema)


output_spec_dict['components'] = {
    'responses': component_responses_spec_dict,
    'schemas': component_schemas_spec_dict,
    'securitySchemes': internal_spec_dict['components']['securitySchemes']
}

print(json.dumps(output_spec_dict, indent = 2, sort_keys = True))