SELECT
	name
	, description
	, isDefaultValue
	, isActiveValue
FROM account.${enumClassname}
WHERE
	name = '${name}'