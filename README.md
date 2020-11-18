###Running instructions:
1. in project root do `mvn package`
2. `cd statistics`
3. `mvn spring-boot:run` - this runs the statistics API
4. open another terminal window and `cd reports`
5. `mvn spring-boot:run` - this runs the reports API
#####web client will be available on localhost:8000 (it's running under statistics API server)
optional: if you have node and npm on your machine
1. `cd client`
2. `npm start`
#####this runs the React.js app, which is independent of the other servers (which was the intended approach, but doesn't work on feng-linux)