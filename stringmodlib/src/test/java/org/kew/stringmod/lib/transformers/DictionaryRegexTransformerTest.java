package org.kew.stringmod.lib.transformers;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.kew.stringmod.utils.Dict;
import org.kew.stringmod.utils.OrderedDict;

public class DictionaryRegexTransformerTest extends DictionaryTransformerTest {

    @Test
    @Override
    public void test() throws IOException, TransformationException {
        Dict dict = new OrderedDict();
        dict.setFilePath(this.dictFile.toString());
        dict.setFileDelimiter("&#09;");
        DictionaryRegexTransformer transformer = new DictionaryRegexTransformer();
        transformer.setDict(dict);
        assertThat(transformer.transform("abcdefg"), equalTo("bbcdefg"));
        transformer.setMultiTransform(true);
        assertThat(transformer.transform("abcdefg"), equalTo("bbddefg"));
    }

}
