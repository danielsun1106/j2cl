/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.j2cl.ast.visitors;

import static com.google.common.base.Preconditions.checkState;

import com.google.j2cl.ast.AbstractVisitor;
import com.google.j2cl.ast.BooleanLiteral;
import com.google.j2cl.ast.CompilationUnit;
import com.google.j2cl.ast.MemberDescriptor;
import com.google.j2cl.ast.Node;
import com.google.j2cl.ast.NullLiteral;
import com.google.j2cl.ast.NumberLiteral;
import com.google.j2cl.ast.Statement;
import com.google.j2cl.ast.StringLiteral;
import com.google.j2cl.ast.TypeDeclaration;
import com.google.j2cl.ast.TypeDescriptor;
import com.google.j2cl.ast.VariableReference;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Verifies that nodes only appear once in the AST. AST nodes with mutable state need to appear only
 * once in the tree, otherwise rewriting might be not correct.
 *
 * <p>Example of nodes that do not need to be unique are TypeDescriptor (and other descriptor
 * types), Literals that are actually singletons like BooleanLiterals, NullLiteral, etc.
 *
 * <p>The other special situation are VariableReferences which are allowed to point to the same
 * Variable.
 */
public class VerifySingleAstReference {

  public static void applyTo(CompilationUnit compilationUnit) {
    // This map keeps track of the nodes that have been found so far in the AST as well as the
    // context of their first appearance for better error reporting.
    final Map<Node, Node> contextByNode = new HashMap<>();

    compilationUnit.accept(
        new AbstractVisitor() {
          Deque<Statement> statementStack = new ArrayDeque<>();

          @Override
          public boolean enterMemberDescriptor(MemberDescriptor memberDescriptor) {
            return false;
          }

          @Override
          public boolean enterNode(final Node node) {
            final Node context =
                !statementStack.isEmpty()
                    ? statementStack.peek()
                    : getCurrentMember() != null ? getCurrentMember() : getCurrentType();
            final Node oldContext = contextByNode.get(node);
            // Context might be null (e.g. for Type nodes), so an explicit check for containsKey
            // is needed here.
            checkState(
                !contextByNode.containsKey(node),
                "%s %s in %s was already seen in %s",
                node.getClass().getSimpleName(),
                node,
                context,
                oldContext);
            contextByNode.put(node, context);
            return true;
          }

          // NullLiteral is a singleton and does not need to be unique in the ast.
          @Override
          public boolean enterNullLiteral(NullLiteral nullLiteral) {
            return false;
          }

          // StringLiterals is a value type and does not need to be unique in the ast.
          @Override
          public boolean enterStringLiteral(StringLiteral stringLiteral) {
            return false;
          }

          // NumberLiterals are value types they need not be unique in the ast.
          @Override
          public boolean enterNumberLiteral(NumberLiteral numberLiteral) {
            return false;
          }

          // BooleanLiterals true and false are singleton and does not need to be unique in the ast.
          @Override
          public boolean enterBooleanLiteral(BooleanLiteral booleanLiteral) {
            return false;
          }

          // TypeDeclarations are references and can appear multiple times in the ast.
          @Override
          public boolean enterTypeDeclaration(TypeDeclaration typeDeclaration) {
            return false;
          }

          // TypeDescriptors are references and can appear multiple times in the ast.
          @Override
          public boolean enterTypeDescriptor(TypeDescriptor typeDescriptor) {
            return false;
          }

          @Override
          public boolean enterVariableReference(VariableReference variableReference) {
            // Verify only that the reference is unique.
            enterNode(variableReference);
            return false;
          }

          @Override
          public boolean enterStatement(Statement statement) {
            statementStack.push(statement);
            return true;
          }

          @Override
          public void exitStatement(Statement statement) {
            checkState(statementStack.pop() == statement);
          }
        });
  }
}
