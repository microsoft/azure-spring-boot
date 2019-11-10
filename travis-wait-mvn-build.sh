#!/bin/bash
function write_visual_bells() {
  while true; do
    echo -en "\a"
    sleep 10
  done
}
write_visual_bells&
