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

describe('Transaction e2e test', () => {
  const transactionPageUrl = '/transaction';
  const transactionPageUrlPattern = new RegExp('/transaction(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const transactionSample = { transactionType: 'BUY', quantity: 739.7, price: 22978.93, timestamp: '2025-01-18T02:32:10.927Z' };

  let transaction;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/transactions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/transactions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/transactions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (transaction) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/transactions/${transaction.id}`,
      }).then(() => {
        transaction = undefined;
      });
    }
  });

  it('Transactions menu should load Transactions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('transaction');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Transaction').should('exist');
    cy.url().should('match', transactionPageUrlPattern);
  });

  describe('Transaction page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(transactionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Transaction page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/transaction/new$'));
        cy.getEntityCreateUpdateHeading('Transaction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', transactionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/transactions',
          body: transactionSample,
        }).then(({ body }) => {
          transaction = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/transactions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/transactions?page=0&size=20>; rel="last",<http://localhost/api/transactions?page=0&size=20>; rel="first"',
              },
              body: [transaction],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(transactionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Transaction page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('transaction');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', transactionPageUrlPattern);
      });

      it('edit button click should load edit Transaction page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Transaction');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', transactionPageUrlPattern);
      });

      it('edit button click should load edit Transaction page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Transaction');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', transactionPageUrlPattern);
      });

      it('last delete button click should delete instance of Transaction', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('transaction').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', transactionPageUrlPattern);

        transaction = undefined;
      });
    });
  });

  describe('new Transaction page', () => {
    beforeEach(() => {
      cy.visit(`${transactionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Transaction');
    });

    it('should create an instance of Transaction', () => {
      cy.get(`[data-cy="transactionType"]`).select('BUY');

      cy.get(`[data-cy="quantity"]`).type('30882.84');
      cy.get(`[data-cy="quantity"]`).should('have.value', '30882.84');

      cy.get(`[data-cy="price"]`).type('13218.36');
      cy.get(`[data-cy="price"]`).should('have.value', '13218.36');

      cy.get(`[data-cy="timestamp"]`).type('2025-01-17T23:36');
      cy.get(`[data-cy="timestamp"]`).blur();
      cy.get(`[data-cy="timestamp"]`).should('have.value', '2025-01-17T23:36');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        transaction = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', transactionPageUrlPattern);
    });
  });
});
