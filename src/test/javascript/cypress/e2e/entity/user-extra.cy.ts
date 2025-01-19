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

describe('UserExtra e2e test', () => {
  const userExtraPageUrl = '/user-extra';
  const userExtraPageUrlPattern = new RegExp('/user-extra(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const userExtraSample = { fullName: 'finally' };

  let userExtra;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/user-extras+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/user-extras').as('postEntityRequest');
    cy.intercept('DELETE', '/api/user-extras/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (userExtra) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/user-extras/${userExtra.id}`,
      }).then(() => {
        userExtra = undefined;
      });
    }
  });

  it('UserExtras menu should load UserExtras page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('user-extra');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('UserExtra').should('exist');
    cy.url().should('match', userExtraPageUrlPattern);
  });

  describe('UserExtra page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(userExtraPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create UserExtra page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/user-extra/new$'));
        cy.getEntityCreateUpdateHeading('UserExtra');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userExtraPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/user-extras',
          body: userExtraSample,
        }).then(({ body }) => {
          userExtra = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/user-extras+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [userExtra],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(userExtraPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details UserExtra page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('userExtra');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userExtraPageUrlPattern);
      });

      it('edit button click should load edit UserExtra page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserExtra');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userExtraPageUrlPattern);
      });

      it('edit button click should load edit UserExtra page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('UserExtra');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userExtraPageUrlPattern);
      });

      it('last delete button click should delete instance of UserExtra', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('userExtra').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', userExtraPageUrlPattern);

        userExtra = undefined;
      });
    });
  });

  describe('new UserExtra page', () => {
    beforeEach(() => {
      cy.visit(`${userExtraPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('UserExtra');
    });

    it('should create an instance of UserExtra', () => {
      cy.get(`[data-cy="fullName"]`).type('reconsideration nor');
      cy.get(`[data-cy="fullName"]`).should('have.value', 'reconsideration nor');

      cy.get(`[data-cy="phoneNumber"]`).type('why lest drat');
      cy.get(`[data-cy="phoneNumber"]`).should('have.value', 'why lest drat');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        userExtra = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', userExtraPageUrlPattern);
    });
  });
});
