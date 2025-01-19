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

describe('Holding e2e test', () => {
  const holdingPageUrl = '/holding';
  const holdingPageUrlPattern = new RegExp('/holding(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const holdingSample = { symbol: 'charming boo', quantity: 29264.51, averageCost: 16301.42 };

  let holding;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/holdings+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/holdings').as('postEntityRequest');
    cy.intercept('DELETE', '/api/holdings/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (holding) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/holdings/${holding.id}`,
      }).then(() => {
        holding = undefined;
      });
    }
  });

  it('Holdings menu should load Holdings page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('holding');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Holding').should('exist');
    cy.url().should('match', holdingPageUrlPattern);
  });

  describe('Holding page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(holdingPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Holding page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/holding/new$'));
        cy.getEntityCreateUpdateHeading('Holding');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', holdingPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/holdings',
          body: holdingSample,
        }).then(({ body }) => {
          holding = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/holdings+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/holdings?page=0&size=20>; rel="last",<http://localhost/api/holdings?page=0&size=20>; rel="first"',
              },
              body: [holding],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(holdingPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Holding page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('holding');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', holdingPageUrlPattern);
      });

      it('edit button click should load edit Holding page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Holding');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', holdingPageUrlPattern);
      });

      it('edit button click should load edit Holding page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Holding');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', holdingPageUrlPattern);
      });

      it('last delete button click should delete instance of Holding', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('holding').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', holdingPageUrlPattern);

        holding = undefined;
      });
    });
  });

  describe('new Holding page', () => {
    beforeEach(() => {
      cy.visit(`${holdingPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Holding');
    });

    it('should create an instance of Holding', () => {
      cy.get(`[data-cy="symbol"]`).type('among without transcend');
      cy.get(`[data-cy="symbol"]`).should('have.value', 'among without transcend');

      cy.get(`[data-cy="quantity"]`).type('10240.98');
      cy.get(`[data-cy="quantity"]`).should('have.value', '10240.98');

      cy.get(`[data-cy="averageCost"]`).type('8298.98');
      cy.get(`[data-cy="averageCost"]`).should('have.value', '8298.98');

      cy.get(`[data-cy="currentPrice"]`).type('37.25');
      cy.get(`[data-cy="currentPrice"]`).should('have.value', '37.25');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        holding = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', holdingPageUrlPattern);
    });
  });
});
