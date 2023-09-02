# planregister-frontend

```
docker-compose up
```

```
java -jar /Users/stefan/Downloads/ili2pg-4.11.0.jar --dbhost localhost --dbdatabase edit --dbport 54321 --dbusr ddluser --dbpwd ddluser --defaultSrsCode 2056 --strokeArcs --createEnumTabs --createMetaInfo --createImportTabs	--createBasketCol --createFk --createFkIdx --models SO_Nutzungsplanung_Planregister_Publikation_20221115 --dbschema arp_nutzungsplanung_planregister_v1 --schemaimport
```

```
java -jar /Users/stefan/Downloads/ili2pg-4.11.0.jar --dbhost localhost --dbdatabase edit --dbport 54321 --dbusr ddluser --dbpwd ddluser --models SO_Nutzungsplanung_Planregister_Publikation_20221115 --dbschema arp_nutzungsplanung_planregister_v1 --deleteData --import /Users/stefan/Downloads/planregister.xtf
```