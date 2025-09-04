#!/bin/bash

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== Kafka Docker Compose Setup Script ===${NC}"
echo ""

# 디렉토리 생성
echo -e "${YELLOW}Creating directories...${NC}"
mkdir -p docker/kafka

# docker-compose-kafka.yml 파일 생성
echo -e "${YELLOW}Creating docker-compose-kafka.yml...${NC}"
cat > docker-compose-kafka.yml << 'EOF'
version: '3.8'

services:
  kafka-1:
    image: confluentinc/cp-kafka:7.6.0
    container_name: kafka-1
    ports:
      - "9092:9092"   # 외부 접근용 (맥에서 localhost:9092)
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      # 리스너 정의
      KAFKA_LISTENERS: INTERNAL://0.0.0.0:29092,EXTERNAL://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka-1:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka-1:9093,2@kafka-2:9093,3@kafka-3:9093
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_CLUSTER_ID: K24o1mnaTw-kfSNOhK9PQg
      CLUSTER_ID: K24o1mnaTw-kfSNOhK9PQg
    volumes:
      - ./docker/kafka/kafka-init.sh:/kafka-init.sh
      - kafka1-data:/var/lib/kafka/data
    entrypoint: ["/bin/bash", "/kafka-init.sh"]
    networks:
      - kafka-net

  kafka-2:
    image: confluentinc/cp-kafka:7.6.0
    container_name: kafka-2
    ports:
      - "9094:9094"
    environment:
      KAFKA_NODE_ID: 2
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_LISTENERS: INTERNAL://0.0.0.0:29094,EXTERNAL://0.0.0.0:9094,CONTROLLER://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka-2:29094
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka-1:9093,2@kafka-2:9093,3@kafka-3:9093
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_CLUSTER_ID: K24o1mnaTw-kfSNOhK9PQg
      CLUSTER_ID: K24o1mnaTw-kfSNOhK9PQg
    volumes:
      - ./docker/kafka/kafka-init.sh:/kafka-init.sh
      - kafka2-data:/var/lib/kafka/data
    entrypoint: ["/bin/bash", "/kafka-init.sh"]
    networks:
      - kafka-net

  kafka-3:
    image: confluentinc/cp-kafka:7.6.0
    container_name: kafka-3
    ports:
      - "9096:9096"
    environment:
      KAFKA_NODE_ID: 3
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_LISTENERS: INTERNAL://0.0.0.0:29096,EXTERNAL://0.0.0.0:9096,CONTROLLER://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka-3:29096
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka-1:9093,2@kafka-2:9093,3@kafka-3:9093
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_CLUSTER_ID: K24o1mnaTw-kfSNOhK9PQg
      CLUSTER_ID: K24o1mnaTw-kfSNOhK9PQg
    volumes:
      - ./docker/kafka/kafka-init.sh:/kafka-init.sh
      - kafka3-data:/var/lib/kafka/data
    entrypoint: ["/bin/bash", "/kafka-init.sh"]
    networks:
      - kafka-net

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: kafka-ui
    ports:
      - "8081:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: kraft-cluster
      # 버전에 따라 아래 두 가지 시도 필요
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka-1:29092,kafka-2:29094,kafka-3:29096
      # KAFKA_CLUSTERS_0_BOOTSTRAP_SERVERS: kafka-1:29092,kafka-2:29094,kafka-3:29096
      KAFKA_CLUSTERS_0_PROPERTIES_SECURITY_PROTOCOL: PLAINTEXT
      KAFKA_CLUSTERS_0_PROPERTIES_CLIENT_DNS_LOOKUP: use_all_dns_ips
    depends_on:
      - kafka-1
      - kafka-2
      - kafka-3
    networks:
      - kafka-net

volumes:
  kafka1-data:
  kafka2-data:
  kafka3-data:

networks:
  kafka-net:
    name: kafka-net
EOF

# kafka-init.sh 파일 생성
echo -e "${YELLOW}Creating docker/kafka/kafka-init.sh...${NC}"
cat > docker/kafka/kafka-init.sh << 'EOF'
#!/bin/bash
set -e

DATA_DIR="/var/lib/kafka/data"

if [ -z "$(ls -A $DATA_DIR)" ]; then
  echo "Formatting storage directory for Kafka Node ID ${KAFKA_NODE_ID}..."

  kafka-storage format \
    --ignore-formatted \
    --cluster-id "${KAFKA_CLUSTER_ID}" \
    --config /etc/kafka/kraft/server.properties
else
  echo "Storage directory already formatted, skipping..."
fi

exec /etc/confluent/docker/run
EOF

# kafka-init.sh 파일에 실행 권한 부여
chmod +x docker/kafka/kafka-init.sh

echo ""
echo -e "${GREEN}✅ Files created successfully:${NC}"
echo -e "  📁 docker-compose-kafka.yml"
echo -e "  📁 docker/kafka/kafka-init.sh"
echo ""
echo -e "${BLUE}Usage:${NC}"
echo -e "  ${YELLOW}docker-compose -f docker-compose-kafka.yml up -d${NC}"
echo -e "  ${YELLOW}docker-compose -f docker-compose-kafka.yml down${NC}"
echo ""
echo -e "${BLUE}Access points:${NC}"
echo -e "  🚀 Kafka UI: http://localhost:8081"
echo -e "  🔌 Kafka Brokers:"
echo -e "    - kafka-1: localhost:9092"
echo -e "    - kafka-2: localhost:9094"
echo -e "    - kafka-3: localhost:9096"
echo ""