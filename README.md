# Naikeri HRR (Home Re-Routing)

A MAP and CAP proxy for roaming, built on the [Naikeri Signaling Gateway](https://github.com/FerUy/naikeri-signaling-gateway).

- **MAP proxy**: relays MAP between a visited and a home network, rewriting IMSIs and SCCP Global Titles by
  configurable rules. It handles the attach procedure (Send-Authentication-Info, Update-Location and
  Insert-Subscriber-Data, and Update-GPRS-Location for packet data), MO- and MT-Forward-Short-Message, and
  Provide-Roaming-Number, with each result relayed back the same way.
- **CAP proxy**: CAMEL Home Re-Routing, steering a roaming subscriber's calls through the home network's SCP.

The call flows are described in the admin guide, which every release carries as HTML and PDF.

## Requirements

- Java 11
- Maven 3.9, and Ant to build a release
- Linux with SCTP support (the `lksctp-tools` package, or the kernel's `sctp` module), to run it

## Building

~~~
mvn clean install
~~~

The jar lands in `home-re-routing/target/`, with its dependencies in `home-re-routing/target/lib/`.

The admin guide is a separate build; `-Pall` adds the PDF to the HTML:

~~~
mvn -f docs/pom.xml clean install -Pall
~~~

## Releasing

~~~
cd release && ant
~~~

builds HRR and the admin guide and packages them as `release/Naikeri-HRR-<version>.zip`:

| Directory | Contents |
|---|---|
| `bin/` | the HRR jar, its dependencies in `lib/`, and `start.sh` |
| `conf/` | the CAP and MAP proxy configurations, their rules, and `log4j2.xml` |
| `docs/adminguide/` | the admin guide, as `html-book/` and `pdf/` |
| `logs/` | where HRR writes its logs and CDRs |

Jenkins builds the same zip for every commit on master, as `Naikeri-HRR-<version>-<build>.zip`.

## Running

Unpack the release anywhere and start it with the configuration to run:

~~~
bin/start.sh extended-signaling-gateway.xml        # the CAP proxy
bin/start.sh extended-signaling-gateway_map.xml    # the MAP proxy
~~~

With no argument, or one that isn't in `conf/`, `start.sh` lists the configurations available. It reads
the configuration from `conf/`, logs to `logs/` (`debugfile.log`, `errorfile.log`, the CDR files, and the
console output in `console.out`), and stops cleanly on Ctrl-C or SIGTERM. `JAVA_OPTS`, if set, replaces
the default heap settings of `-Xms1g -Xmx1g`.

## Docker

`release/docker/Dockerfile` builds an image from a release: unpack the zip beside it, then

~~~
cd release/docker
unzip Naikeri-HRR-<version>.zip
docker build --build-arg HRR_VERSION=<version> -t naikeri-hrr:<version> .
~~~

and run it on the host's network, which suits SCTP, with the configuration as the argument (the CAP proxy
if none is given):

~~~
docker run -d --network host --name hrr \
  -v /var/log/naikeri-hrr:/opt/naikeri/hrr/logs \
  naikeri-hrr:<version> extended-signaling-gateway_map.xml
~~~

The host needs SCTP support. To run with a deployment's own configuration, mount it over
`/opt/naikeri/hrr/conf`. `docker stop hrr` shuts HRR down cleanly.

## Testing with the simulators

`home-re-routing/src/test/java` holds simulators for both proxies: `MapSimulator` plays the network
elements at both ends of each MAP flow and answers every request, and `CapSimulator` plays the visited and
home sides of the CAP flow. Run HRR with the matching configuration, then the simulator, for example from
the IDE with `-DmainConfig.path` pointing at `home-re-routing/src/main/resources`:

| Proxy | HRR's argument | Simulator |
|---|---|---|
| MAP | `extended-signaling-gateway_map.xml` | `MapSimulator` (`map-simulator-config.xml`) |
| CAP | `extended-signaling-gateway.xml` | `CapSimulator` (`cap-simulator-config.xml`) |

jSS7 keeps M3UA, SCCP, SCTP and TCAP state in XML files in the working directory; remove them between runs
that change the configuration.

## License

Licensed under the GNU Affero General Public License v3.0; see [LICENSE](LICENSE).
