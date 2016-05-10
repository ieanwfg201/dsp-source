PERIODIC MAINTENANCE
--------------------

ANALYZE table <table_name>
OPTIMIZE table <table_name>




This module contains demand data model like ad, campaign, advertiser, account, budget, ad_metadata.

hierarchy

advertiser,publisher -> partner_account -> campaign,site -> ad
-- budget tables, billing tables,revenue share tables

The advertiser and publisher are partners, whose properties, attributes differ a lot however have same basic
info so keep them at one place as partner, keep type, other meta tables related to them are kept separate.
