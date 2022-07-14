/*
 * MIT License
 *
 * Copyright (c) 2021 Bingqi Ji
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.guinsoo.ext;

import org.immutables.value.Value;
import java.util.List;
import java.util.Set;


public class ImmutableItem {

//    public static void main(String[] args) {
//        FoobarValue value = ImmutableFoobarValue.builder()
//                .foo(2)
//                .bar("Bar")
//                .addBuz(1, 3, 4)
//                .build(); // FoobarValue{foo=2, bar=Bar, buz=[1, 3, 4], crux={}}
//
//        int foo = value.foo(); // 2
//        System.out.println(foo);
//
//        List<Integer> buz = value.buz();
//        System.out.println(buz);
//    }
    
}


@Value.Immutable
abstract class FoobarValue {
    public abstract int foo();
    public abstract String bar();
    public abstract List<Integer> buz();
    public abstract Set<Long> crux();
}