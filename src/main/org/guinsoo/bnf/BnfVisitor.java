/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.bnf;

import java.util.ArrayList;

/**
 * The visitor interface for BNF rules.
 */
public interface BnfVisitor {

    /**
     * Visit a rule element.
     *
     * @param keyword whether this is a keyword
     * @param name the element name
     * @param link the linked rule if it's not a keyword
     */
    void visitRuleElement(boolean keyword, String name, Rule link);

    /**
     * Visit a repeat rule.
     *
     * @param comma whether the comma is repeated as well
     * @param rule the element to repeat
     */
    void visitRuleRepeat(boolean comma, Rule rule);

    /**
     * Visit a fixed rule.
     *
     * @param type the type
     */
    void visitRuleFixed(int type);

    /**
     * Visit a rule list.
     *
     * @param or true for OR, false for AND
     * @param list the rules
     */
    void visitRuleList(boolean or, ArrayList<Rule> list);

    /**
     * Visit an optional rule.
     *
     * @param rule the rule
     */
    void visitRuleOptional(Rule rule);

    /**
     * Visit an OR list of optional rules.
     *
     * @param list the optional rules
     */
    void visitRuleOptional(ArrayList<Rule> list);

    /**
     * Visit a rule with non-standard extension.
     *
     * @param rule the rule
     * @param compatibility whether this rule exists for compatibility only
     */
    void visitRuleExtension(Rule rule, boolean compatibility);

}
