package tn.soretras.articlesmanager.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tn.soretras.articlesmanager.IntegrationTest;
import tn.soretras.articlesmanager.domain.Article;
import tn.soretras.articlesmanager.repository.ArticleRepository;
import tn.soretras.articlesmanager.service.dto.ArticleDTO;
import tn.soretras.articlesmanager.service.mapper.ArticleMapper;

/**
 * Integration tests for the {@link ArticleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ArticleResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String DEFAULT_IDCATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_IDCATEGORY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/articles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private MockMvc restArticleMockMvc;

    private Article article;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Article createEntity() {
        Article article = new Article().name(DEFAULT_NAME).price(DEFAULT_PRICE).quantity(DEFAULT_QUANTITY).idcategory(DEFAULT_IDCATEGORY);
        return article;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Article createUpdatedEntity() {
        Article article = new Article().name(UPDATED_NAME).price(UPDATED_PRICE).quantity(UPDATED_QUANTITY).idcategory(UPDATED_IDCATEGORY);
        return article;
    }

    @BeforeEach
    public void initTest() {
        articleRepository.deleteAll();
        article = createEntity();
    }

    @Test
    void createArticle() throws Exception {
        int databaseSizeBeforeCreate = articleRepository.findAll().size();
        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);
        restArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(articleDTO)))
            .andExpect(status().isCreated());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeCreate + 1);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testArticle.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testArticle.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testArticle.getIdcategory()).isEqualTo(DEFAULT_IDCATEGORY);
    }

    @Test
    void createArticleWithExistingId() throws Exception {
        // Create the Article with an existing ID
        article.setId("existing_id");
        ArticleDTO articleDTO = articleMapper.toDto(article);

        int databaseSizeBeforeCreate = articleRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(articleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = articleRepository.findAll().size();
        // set the field null
        article.setName(null);

        // Create the Article, which fails.
        ArticleDTO articleDTO = articleMapper.toDto(article);

        restArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(articleDTO)))
            .andExpect(status().isBadRequest());

        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllArticles() throws Exception {
        // Initialize the database
        articleRepository.save(article);

        // Get all the articleList
        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(article.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].idcategory").value(hasItem(DEFAULT_IDCATEGORY)));
    }

    @Test
    void getArticle() throws Exception {
        // Initialize the database
        articleRepository.save(article);

        // Get the article
        restArticleMockMvc
            .perform(get(ENTITY_API_URL_ID, article.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(article.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.idcategory").value(DEFAULT_IDCATEGORY));
    }

    @Test
    void getNonExistingArticle() throws Exception {
        // Get the article
        restArticleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingArticle() throws Exception {
        // Initialize the database
        articleRepository.save(article);

        int databaseSizeBeforeUpdate = articleRepository.findAll().size();

        // Update the article
        Article updatedArticle = articleRepository.findById(article.getId()).get();
        updatedArticle.name(UPDATED_NAME).price(UPDATED_PRICE).quantity(UPDATED_QUANTITY).idcategory(UPDATED_IDCATEGORY);
        ArticleDTO articleDTO = articleMapper.toDto(updatedArticle);

        restArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, articleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(articleDTO))
            )
            .andExpect(status().isOk());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArticle.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testArticle.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testArticle.getIdcategory()).isEqualTo(UPDATED_IDCATEGORY);
    }

    @Test
    void putNonExistingArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().size();
        article.setId(UUID.randomUUID().toString());

        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, articleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(articleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().size();
        article.setId(UUID.randomUUID().toString());

        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(articleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().size();
        article.setId(UUID.randomUUID().toString());

        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(articleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateArticleWithPatch() throws Exception {
        // Initialize the database
        articleRepository.save(article);

        int databaseSizeBeforeUpdate = articleRepository.findAll().size();

        // Update the article using partial update
        Article partialUpdatedArticle = new Article();
        partialUpdatedArticle.setId(article.getId());

        partialUpdatedArticle.name(UPDATED_NAME).price(UPDATED_PRICE).quantity(UPDATED_QUANTITY);

        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedArticle))
            )
            .andExpect(status().isOk());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArticle.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testArticle.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testArticle.getIdcategory()).isEqualTo(DEFAULT_IDCATEGORY);
    }

    @Test
    void fullUpdateArticleWithPatch() throws Exception {
        // Initialize the database
        articleRepository.save(article);

        int databaseSizeBeforeUpdate = articleRepository.findAll().size();

        // Update the article using partial update
        Article partialUpdatedArticle = new Article();
        partialUpdatedArticle.setId(article.getId());

        partialUpdatedArticle.name(UPDATED_NAME).price(UPDATED_PRICE).quantity(UPDATED_QUANTITY).idcategory(UPDATED_IDCATEGORY);

        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedArticle))
            )
            .andExpect(status().isOk());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
        Article testArticle = articleList.get(articleList.size() - 1);
        assertThat(testArticle.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testArticle.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testArticle.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testArticle.getIdcategory()).isEqualTo(UPDATED_IDCATEGORY);
    }

    @Test
    void patchNonExistingArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().size();
        article.setId(UUID.randomUUID().toString());

        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, articleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(articleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().size();
        article.setId(UUID.randomUUID().toString());

        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(articleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamArticle() throws Exception {
        int databaseSizeBeforeUpdate = articleRepository.findAll().size();
        article.setId(UUID.randomUUID().toString());

        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(articleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Article in the database
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteArticle() throws Exception {
        // Initialize the database
        articleRepository.save(article);

        int databaseSizeBeforeDelete = articleRepository.findAll().size();

        // Delete the article
        restArticleMockMvc
            .perform(delete(ENTITY_API_URL_ID, article.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Article> articleList = articleRepository.findAll();
        assertThat(articleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
