SELECT
	name
	, description
	, active
FROM account.${enumClassname}
WHERE
	name = '${name}'