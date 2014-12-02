World Checklist to IPNI matching
================================

These configurations were used by the SHS Data Improvement Team to match [World Checklist](http://apps.kew.org/wcsp/) names to names in [IPNI](http://www.ipni.org/).

They were developed in November 2013, so it's possible there are slight differences in the configuration — the RMF Core was refactored since that time.

The configurations were produced and executed using [MatchConf](../../../../../../../../matchconf).  They can be run as a standalone configuration with
  mvn exec:java -Dexec.args="--config-file src/main/resources/META-INF/spring/rmf-configurations/wcs-match/config_WCS_match0.xml"
or similar.  Check file paths carefully to avoid overwriting existing data!

Results and further information are in `T:\Development\SHS Project\Data Improvement\Matching_WCS\final_matching`.
