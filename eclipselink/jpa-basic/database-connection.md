Download PostgreSQL from [here](https://www.postgresql.org/download/)

You need to specify a database url, username, and password to have a local database connection

You can create a new database by opening up pgAdmin and right clicking the PostgreSQL
server in the browser list on the left -> Create -> Database. Here you can specify the
name and the owner. If you want create a new user to be the owner you can right click 
again in the same  place -> Create -> Login/Group role and specify username, password
and the permissions of that user. 

Go to IntelliJ -> Run -> Edit Configurations and find the Spring Boot template

Under "Environment variables" paste this line:

DATABASE_URL=your_db_url;DATABASE_USERNAME=your_db_username;DATABASE_PASSWORD=your_db_password

Replace the "your_db..." values with your actual values that you created in pgAdmin.

The database url probably has a form of jdbc:postgresql://localhost:5432/databasename

Click apply then ok and do the same for the JUnit template (you may need to manually
edit preexisting configurations of tests you've ran previously)

Now when you run the program it should have a local database connection
