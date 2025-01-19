import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Portfolio e2e test', () => {
  const portfolioPageUrl = '/portfolio';
  const portfolioPageUrlPattern = new RegExp('/portfolio(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const portfolioSample = { name: 'chase sympathetically dramatize' };

  let portfolio;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/portfolios+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/portfolios').as('postEntityRequest');
    cy.intercept('DELETE', '/api/portfolios/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (portfolio) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/portfolios/${portfolio.id}`,
      }).then(() => {
        portfolio = undefined;
      });
    }
  });

  it('Portfolios menu should load Portfolios page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('portfolio');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Portfolio').should('exist');
    cy.url().should('match', portfolioPageUrlPattern);
  });

  describe('Portfolio page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(portfolioPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Portfolio page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/portfolio/new$'));
        cy.getEntityCreateUpdateHeading('Portfolio');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/portfolios',
          body: portfolioSample,
        }).then(({ body }) => {
          portfolio = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/portfolios+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/portfolios?page=0&size=20>; rel="last",<http://localhost/api/portfolios?page=0&size=20>; rel="first"',
              },
              body: [portfolio],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(portfolioPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Portfolio page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('portfolio');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioPageUrlPattern);
      });

      it('edit button click should load edit Portfolio page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Portfolio');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioPageUrlPattern);
      });

      it('edit button click should load edit Portfolio page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Portfolio');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioPageUrlPattern);
      });

      it('last delete button click should delete instance of Portfolio', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('portfolio').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', portfolioPageUrlPattern);

        portfolio = undefined;
      });
    });
  });

  describe('new Portfolio page', () => {
    beforeEach(() => {
      cy.visit(`${portfolioPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Portfolio');
    });

    it('should create an instance of Portfolio', () => {
      cy.get(`[data-cy="name"]`).type('lighthearted');
      cy.get(`[data-cy="name"]`).should('have.value', 'lighthearted');

      cy.get(`[data-cy="description"]`).type('political');
      cy.get(`[data-cy="description"]`).should('have.value', 'political');

      cy.get(`[data-cy="totalValue"]`).type('16862.57');
      cy.get(`[data-cy="totalValue"]`).should('have.value', '16862.57');

      cy.get(`[data-cy="lastUpdated"]`).type('2025-01-19T01:20');
      cy.get(`[data-cy="lastUpdated"]`).blur();
      cy.get(`[data-cy="lastUpdated"]`).should('have.value', '2025-01-19T01:20');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        portfolio = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', portfolioPageUrlPattern);
    });
  });
});
