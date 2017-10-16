UPDATE account.${enumClassname}
SET
 isdefault = ${isdefault}
, description = '${description}'
, active = true
WHERE
name = '${name}'
