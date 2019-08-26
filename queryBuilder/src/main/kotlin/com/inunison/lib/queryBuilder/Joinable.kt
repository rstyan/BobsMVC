package com.inunison.lib.queryBuilder

/*
 * element is one that can be used in join expressions
 * such as a join (b join c on b.id=c.id) on ...
 */
interface Joinable : Expression
