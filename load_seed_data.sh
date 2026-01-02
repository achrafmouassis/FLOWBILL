#!/bin/bash
echo "========================================================"
echo " FLOWBILL SEED DATA LOADER"
echo "========================================================"

# Check if docker container is running
if [ ! "$(docker ps -q -f name=postgres-master)" ]; then
    echo "Error: 'postgres-master' container is not running."
    echo "Please run 'docker-compose up -d postgres' first."
    exit 1
fi

echo "Loading data.sql into postgres-master..."
# Copy file to container
docker cp data.sql postgres-master:/tmp/data.sql

# Execute SQL
# We use -U flowbill_user -d flowbill as defined in docker-compose
docker exec -i postgres-master psql -U flowbill_user -d flowbill -f /tmp/data.sql

if [ $? -eq 0 ]; then
    echo "✅ Data loaded successfully!"
    echo "You can now login as:"
    echo "  - superadmin@flowbill.com / password"
    echo "  - admin@acme.com / password"
    echo "  - ceo@startupflow.io / password"
else
    echo "❌ Failed to load data."
fi
