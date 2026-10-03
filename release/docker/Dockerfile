FROM amazoncorretto:11-alpine

LABEL maintainer="Fernando Mendioroz <fernando.mendioroz@gmail.com>"

# bash runs bin/start.sh; lksctp-tools provides the SCTP library M3UA runs over
RUN apk add --no-cache bash lksctp-tools

# The release, unpacked beside this Dockerfile: unzip Naikeri-HRR-<version>.zip here, then
#   docker build --build-arg HRR_VERSION=<version> -t naikeri-hrr:<version> .
ARG HRR_VERSION
COPY Naikeri-HRR-${HRR_VERSION}/. /opt/naikeri/hrr/
WORKDIR /opt/naikeri/hrr

# Configuration and logs at fixed paths, to mount a deployment's own conf/ and keep its logs.
# The jSS7 state files bin/ collects are left to the container, so each start begins clean.
VOLUME ["/opt/naikeri/hrr/logs"]

# Replaces start.sh's default heap settings when set: docker run -e JAVA_OPTS="-Xms2g -Xmx2g" ...
ENV JAVA_OPTS=""

# The argument names the configuration in conf/: the CAP proxy unless another is given, e.g.
#   docker run --network host naikeri-hrr:<version> map-proxy.xml
ENTRYPOINT ["/opt/naikeri/hrr/bin/start.sh"]
CMD ["cap-proxy.xml"]
