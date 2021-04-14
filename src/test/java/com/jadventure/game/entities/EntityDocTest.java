package com.jadventure.game.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.sfvl.doctesting.junitextension.ApprovalsExtension;
import org.sfvl.doctesting.utils.DocWriter;

public class EntityDocTest {

    static DocWriter doc = new DocWriter();
    @RegisterExtension
    static ApprovalsExtension extension = new ApprovalsExtension(doc);

    @Test
    public void testStorage(TestInfo testInfo) {
        doc.write("To implement");
    }
}
