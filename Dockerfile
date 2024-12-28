# Build stage
FROM maven:3.8-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Run stage
FROM openjdk:17-slim

MAINTAINER h0j3n

WORKDIR /app

# Copy the JAR file
COPY --from=build /app/target/chamber.jar .

# Download and install OpenRASP
RUN apt-get update && apt-get install -y wget \
    && wget https://packages.baidu.com/app/openrasp/release/1.3.8-beta/rasp-java.tar.gz -O /tmp/rasp-java.tar.gz \
    && cd /tmp \
    && tar -zxvf rasp-java.tar.gz \
    && java -jar rasp-*/RaspInstall.jar -nodetect -install /app \
    # Download and set up plugin.js
    && wget https://github.com/baidu/openrasp/raw/refs/heads/master/plugins/official/plugin.js -O /tmp/plugin.js \
    && mkdir -p "/app/rasp/plugins" \
    && sed -i 's/all_log: true/all_log: false/g' /tmp/plugin.js \
    && sed -i 's/log_event: false/log_event: true/g' /tmp/plugin.js \
    && mv /tmp/plugin.js "/app/rasp/plugins/official.js" \
    # Add OpenRASP configuration
    && echo $'block.status_code: 403\n\
block.redirect_url: "/block?request_id=%request_id%"\n\
block.content_html: "<script>location.href=\"/block?request_id=%request_id%\"</script>"\n\
inject.custom_headers:\n\
  X-Protected-By: OpenRASP\n\
plugin.timeout.millis: 100\n\
body.maxbytes: 4096\n\
sql.query.limit: 10000\n\
syslog.tag: OpenRASP\n\
syslog.url: ""\n\
log.maxstack: 10\n\
log.maxburst: 100\n\
decompile.enable: true\n\
plugin.filter: true\n\
response.header.html: ""\n\
security.enforce_policy: true' > "/app/rasp/conf/openrasp.yml" \
    # Cleanup
    && rm -rf /tmp/* \
    && apt-get remove -y wget \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Copy flag.txt
COPY flag.txt /flag-REDACTED.txt

EXPOSE 8080

CMD ["java", \
    "--add-opens=java.base/java.lang=ALL-UNNAMED", \
    "--add-opens=java.base/jdk.internal.loader=ALL-UNNAMED", \
    "-javaagent:/app/rasp/rasp.jar", \
    "-jar", "chamber.jar"]