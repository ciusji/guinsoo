/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.bnf;

import java.util.HashMap;

/**
 * Represents a non-standard syntax.
 */
public class RuleExtension implements Rule {

    private final Rule rule;
    private final boolean compatibility;

    private boolean mapSet;

    public RuleExtension(Rule rule, boolean compatibility) {
        this.rule = rule;
        this.compatibility = compatibility;
    }

    @Override
    public void accept(BnfVisitor visitor) {
        visitor.visitRuleExtension(rule, compatibility);
    }

    @Override
    public void setLinks(HashMap<String, RuleHead> ruleMap) {
        if (!mapSet) {
            rule.setLinks(ruleMap);
            mapSet = true;
        }
    }
    @Override
    public boolean autoComplete(Sentence sentence) {
        sentence.stopIfRequired();
        rule.autoComplete(sentence);
        return true;
    }

    @Override
    public String toString() {
        return (compatibility ? "@c@ " : "@h2@ ") + rule.toString();
    }

}
