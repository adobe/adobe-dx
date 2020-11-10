#!/bin/bash
#Copyright 2020 Adobe. All rights reserved.
#This file is licensed to you under the Apache License, Version 2.0 (the "License");
#you may not use this file except in compliance with the License. You may obtain a copy
#of the License at http://www.apache.org/licenses/LICENSE-2.0

#Unless required by applicable law or agreed to in writing, software distributed under
#the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
#OF ANY KIND, either express or implied. See the License for the specific language
#governing permissions and limitations under the License.

if [ -z "$PIPE_PORT" ]; then
  PIPE_PORT=4502
fi

if [ -z "$PIPE_PROTOCOL" ]; then
  PIPE_PROTOCOL=http
fi

if [ -z "$PIPE_HOST" ]; then
  PIPE_HOST=localhost
fi

if [ -z "$PIPE_USER" ]; then
  PIPE_USER=admin:admin
fi

if [ -z "$PIPE_URI" ]; then
	PIPE_URI=/apps/dx/scripts/exec.txt
fi

if [ -f "$1" ]; then
  curl -u $PIPE_USER -F "pipe_cmdfile=@$1" $PIPE_PROTOCOL://$PIPE_HOST:$PIPE_PORT$PIPE_URI
else
	curl -u $PIPE_USER -Fpipe_cmd="$1" $PIPE_PROTOCOL://$PIPE_HOST:$PIPE_PORT$PIPE_URI
fi