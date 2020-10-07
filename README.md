# CORD19 Geo Explorer

## Required Software

- MySQL 
- MongoDB
- Elasticsearch

## Setup

- in your settings.xml add the following properties:
  - `<db.database.url>jdbc-url-to-db</db.database.url>`
  
  - `<db.user>db-user</db.user>`
  - `<db.password>db-password</db.password>`
- `<app.data.path>path to folder in your system</app.data.path>`
  - in this folder place the following two model files from [opennlp](http://opennlp.sourceforge.net/models-1.5/): en-ner-location.bin, en-token.bin
  
## Import Data

To import data, login, click on "Import" and paste the absolute path to the folder that contains the files to import. That folder should also contain the metadata.csv file that the Allen Institute provides.
