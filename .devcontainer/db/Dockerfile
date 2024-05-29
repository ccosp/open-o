FROM mariadb:10.5

# Labels
LABEL author="chitrank dave"
LABEL version=0.0.1
LABEL description="Oscar-db Docker Image for Development Environment"

# Adding required files
ADD ./.devcontainer/db/scripts/populate_db.sh /docker-entrypoint-initdb.d/populate_db.sh
ADD  ./database /database
ADD ./.devcontainer/development/config/shared/my.cnf /etc/mysql/my.cnf

# Change permissions
RUN chmod 644 /etc/mysql/my.cnf

# Ports
EXPOSE 3306
