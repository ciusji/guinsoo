/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.java;

/**
 * A test application.
 */
public class TestApp {

/* c:

int main(int argc, char** argv) {
//    org_guinsoo_java_TestApp_main(0);
    org_guinsoo_java_TestApp_main(ptr<array<ptr<java_lang_String> > >());
}

*/

    /**
     * Run this application.
     *
     * @param args the command line arguments
     */
    public static void main(String... args) {
        String[] list = new String[1000];
        for (int i = 0; i < 1000; i++) {
            list[i] = "Hello " + i;
        }

        // time:29244000 mac g++ -O3 without array bound checks
        // time:30673000 mac java
        // time:32449000 mac g++ -O3
        // time:69692000 mac g++ -O3 ref counted
        // time:1200000000 raspberry g++ -O3
        // time:1720000000 raspberry g++ -O3 ref counted
        // time:1980469000 raspberry java IcedTea6 1.8.13 Cacao VM
        // time:12962645810 raspberry java IcedTea6 1.8.13 Zero VM
        // java -XXaltjvm=cacao

        for (int k = 0; k < 4; k++) {
            long t = System.nanoTime();
            long h = 0;
            for (int j = 0; j < 10000; j++) {
                for (int i = 0; i < 1000; i++) {
                    String s = list[i];
                    h = (h * 7) ^ s.hashCode();
                }
            }
            System.out.println("hash: " + h);
            t = System.nanoTime() - t;
            System.out.println("time:" + t);
        }
    }

}
