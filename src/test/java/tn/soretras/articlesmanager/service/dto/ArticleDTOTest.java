package tn.soretras.articlesmanager.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tn.soretras.articlesmanager.web.rest.TestUtil;

class ArticleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ArticleDTO.class);
        ArticleDTO articleDTO1 = new ArticleDTO();
        articleDTO1.setId("id1");
        ArticleDTO articleDTO2 = new ArticleDTO();
        assertThat(articleDTO1).isNotEqualTo(articleDTO2);
        articleDTO2.setId(articleDTO1.getId());
        assertThat(articleDTO1).isEqualTo(articleDTO2);
        articleDTO2.setId("id2");
        assertThat(articleDTO1).isNotEqualTo(articleDTO2);
        articleDTO1.setId(null);
        assertThat(articleDTO1).isNotEqualTo(articleDTO2);
    }
}
