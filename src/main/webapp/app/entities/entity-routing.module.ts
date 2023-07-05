import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'article',
        data: { pageTitle: 'Articles' },
        loadChildren: () => import('./articlesdb/article/article.module').then(m => m.ArticlesdbArticleModule),
      },
      {
        path: 'category',
        data: { pageTitle: 'Categories' },
        loadChildren: () => import('./categoriesdb/category/category.module').then(m => m.CategoriesdbCategoryModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
