function write_visual_bells() {
  while true; do
    echo -en "\a"
    sleep 10
  done
}
write_visual_bells&

set -o pipefail && mvn -P travis-ci-test clean cobertura:cobertura-integration-test | grep -v "DEBUG"