JSON File Harvester Client for Redbox/Mint - Using Spring Integration
================================================================================

This application monitors the "input" directory for JSON files and sends the contents over to the ActiveMQ channel. The file is then moved over to the "output" directory.

The implementation does not validate the JSON structure prior to sending and assumes it is successfully harvested.
   
You can run the application by either

* running the "Main" class from within STS (Right-click on Main class --> Run As --> Java Application)
* or from the command line:
    - mvn package
    - mvn exec:java

--------------------------------------------------------------------------------


