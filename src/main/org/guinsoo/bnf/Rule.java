/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.bnf;

import java.util.HashMap;

/**
 * Represents a BNF rule.
 */
public interface Rule {

    /**
     * Update cross references.
     *
     * @param ruleMap the reference map
     */
    void setLinks(HashMap<String, RuleHead> ruleMap);

    /**
     * Add the next possible token(s). If there was a match, the query in the
     * sentence is updated (the matched token is removed).
     *
     * @param sentence the sentence context
     * @return true if a full match
     */
    boolean autoComplete(Sentence sentence);

    /**
     * Call the visit method in the given visitor.
     *
     * @param visitor the visitor
     */
    void accept(BnfVisitor visitor);

}
