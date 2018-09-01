UPDATE account.${enumClassname}
SET
	description = '${description}'
	, isDefaultValue = ${isDefaultValue}
	, isActiveValue = ${isActiveValue}
WHERE
	name = '${name}'
