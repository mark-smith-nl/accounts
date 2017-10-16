SELECT
	name
	, isdefault
	, description
	, active
FROM account.${enumClassname}
WHERE
	name = '${name}'