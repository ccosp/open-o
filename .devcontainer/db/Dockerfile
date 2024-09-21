FROM mariadb:10.5

# Labels
LABEL author="OpenOSP"
LABEL version=0.0.2
LABEL description="OpenOSP-EMR Docker Image for Development Environment"

# Install required packages
RUN apt-get update && apt-get install -y dos2unix  \
    && apt-get autoclean \
    && apt-get clean \
    && apt-get autoremove

# Adding required files
# Currently this needs the database configuration for post-scripts from the OpenOSP-EMR
# repository it is building a database for; this build process could use fine tuning for
# building older versions with different DB schema.  This works, but further work here is
# a TODO.

COPY ./.devcontainer/db/scripts/populate_db.sh /docker-entrypoint-initdb.d/populate_db.sh
COPY ./database /database
COPY ./.devcontainer/development/config/shared/my.cnf /etc/mysql/my.cnf

# Change permissions
RUN chmod 644 /etc/mysql/my.cnf

# Converting script files using dos2unix (Required for Windows)
RUN dos2unix /docker-entrypoint-initdb.d/populate_db.sh
RUN dos2unix /database/mysql/*.sh

# Ports
EXPOSE 3306
