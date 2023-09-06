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

```
SELECT 
    *
FROM 
    arp_nutzungsplanung_planregister_v1.dokument 
WHERE 
    bezeichnung ILIKE '%Stöckliquelle%'
;

CREATE INDEX IF NOT EXISTS idx_gin_dokument_planungsinstrument on arp_nutzungsplanung_planregister_v1.dokument using gin (planungsinstrument gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_gin_dokument_bezeichnung on arp_nutzungsplanung_planregister_v1.dokument using gin (bezeichnung gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_gin_dokument_planungsbehoerde on arp_nutzungsplanung_planregister_v1.dokument using gin (planungsbehoerde gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_gin_dokument_gemeinde on arp_nutzungsplanung_planregister_v1.dokument using gin (gemeinde gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_gin_dokument_rrb_nr on arp_nutzungsplanung_planregister_v1.dokument using gin (rrb_nr gin_trgm_ops);

SELECT 
    *
FROM 
    arp_nutzungsplanung_planregister_v1.dokument 
WHERE 
    bezeichnung ILIKE '%Wasser%'
AND 
    rechtskraft_ab > '2010-01-01' AND rechtskraft_ab < '2016-01-01'
AND 
    rechtsstatus = 'in Kraft'
;
```