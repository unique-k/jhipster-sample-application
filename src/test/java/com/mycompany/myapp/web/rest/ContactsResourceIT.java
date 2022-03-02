package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Contacts;
import com.mycompany.myapp.repository.ContactsRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link ContactsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContactsResourceIT {

    private static final String DEFAULT_FIRSTNAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRSTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_LASTNAME = "AAAAAAAAAA";
    private static final String UPDATED_LASTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/contacts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ContactsRepository contactsRepository;

    @Autowired
    private MockMvc restContactsMockMvc;

    private Contacts contacts;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contacts createEntity() {
        Contacts contacts = new Contacts().firstname(DEFAULT_FIRSTNAME).lastname(DEFAULT_LASTNAME).email(DEFAULT_EMAIL);
        return contacts;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contacts createUpdatedEntity() {
        Contacts contacts = new Contacts().firstname(UPDATED_FIRSTNAME).lastname(UPDATED_LASTNAME).email(UPDATED_EMAIL);
        return contacts;
    }

    @BeforeEach
    public void initTest() {
        contactsRepository.deleteAll();
        contacts = createEntity();
    }

    @Test
    void createContacts() throws Exception {
        int databaseSizeBeforeCreate = contactsRepository.findAll().size();
        // Create the Contacts
        restContactsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contacts)))
            .andExpect(status().isCreated());

        // Validate the Contacts in the database
        List<Contacts> contactsList = contactsRepository.findAll();
        assertThat(contactsList).hasSize(databaseSizeBeforeCreate + 1);
        Contacts testContacts = contactsList.get(contactsList.size() - 1);
        assertThat(testContacts.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testContacts.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testContacts.getEmail()).isEqualTo(DEFAULT_EMAIL);
    }

    @Test
    void createContactsWithExistingId() throws Exception {
        // Create the Contacts with an existing ID
        contacts.setId("existing_id");

        int databaseSizeBeforeCreate = contactsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContactsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contacts)))
            .andExpect(status().isBadRequest());

        // Validate the Contacts in the database
        List<Contacts> contactsList = contactsRepository.findAll();
        assertThat(contactsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllContacts() throws Exception {
        // Initialize the database
        contactsRepository.save(contacts);

        // Get all the contactsList
        restContactsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contacts.getId())))
            .andExpect(jsonPath("$.[*].firstname").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastname").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)));
    }

    @Test
    void getContacts() throws Exception {
        // Initialize the database
        contactsRepository.save(contacts);

        // Get the contacts
        restContactsMockMvc
            .perform(get(ENTITY_API_URL_ID, contacts.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(contacts.getId()))
            .andExpect(jsonPath("$.firstname").value(DEFAULT_FIRSTNAME))
            .andExpect(jsonPath("$.lastname").value(DEFAULT_LASTNAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }

    @Test
    void getNonExistingContacts() throws Exception {
        // Get the contacts
        restContactsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewContacts() throws Exception {
        // Initialize the database
        contactsRepository.save(contacts);

        int databaseSizeBeforeUpdate = contactsRepository.findAll().size();

        // Update the contacts
        Contacts updatedContacts = contactsRepository.findById(contacts.getId()).get();
        updatedContacts.firstname(UPDATED_FIRSTNAME).lastname(UPDATED_LASTNAME).email(UPDATED_EMAIL);

        restContactsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedContacts.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedContacts))
            )
            .andExpect(status().isOk());

        // Validate the Contacts in the database
        List<Contacts> contactsList = contactsRepository.findAll();
        assertThat(contactsList).hasSize(databaseSizeBeforeUpdate);
        Contacts testContacts = contactsList.get(contactsList.size() - 1);
        assertThat(testContacts.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testContacts.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testContacts.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    void putNonExistingContacts() throws Exception {
        int databaseSizeBeforeUpdate = contactsRepository.findAll().size();
        contacts.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contacts.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contacts))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contacts in the database
        List<Contacts> contactsList = contactsRepository.findAll();
        assertThat(contactsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchContacts() throws Exception {
        int databaseSizeBeforeUpdate = contactsRepository.findAll().size();
        contacts.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(contacts))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contacts in the database
        List<Contacts> contactsList = contactsRepository.findAll();
        assertThat(contactsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamContacts() throws Exception {
        int databaseSizeBeforeUpdate = contactsRepository.findAll().size();
        contacts.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(contacts)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Contacts in the database
        List<Contacts> contactsList = contactsRepository.findAll();
        assertThat(contactsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateContactsWithPatch() throws Exception {
        // Initialize the database
        contactsRepository.save(contacts);

        int databaseSizeBeforeUpdate = contactsRepository.findAll().size();

        // Update the contacts using partial update
        Contacts partialUpdatedContacts = new Contacts();
        partialUpdatedContacts.setId(contacts.getId());

        partialUpdatedContacts.firstname(UPDATED_FIRSTNAME).email(UPDATED_EMAIL);

        restContactsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContacts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedContacts))
            )
            .andExpect(status().isOk());

        // Validate the Contacts in the database
        List<Contacts> contactsList = contactsRepository.findAll();
        assertThat(contactsList).hasSize(databaseSizeBeforeUpdate);
        Contacts testContacts = contactsList.get(contactsList.size() - 1);
        assertThat(testContacts.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testContacts.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testContacts.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    void fullUpdateContactsWithPatch() throws Exception {
        // Initialize the database
        contactsRepository.save(contacts);

        int databaseSizeBeforeUpdate = contactsRepository.findAll().size();

        // Update the contacts using partial update
        Contacts partialUpdatedContacts = new Contacts();
        partialUpdatedContacts.setId(contacts.getId());

        partialUpdatedContacts.firstname(UPDATED_FIRSTNAME).lastname(UPDATED_LASTNAME).email(UPDATED_EMAIL);

        restContactsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContacts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedContacts))
            )
            .andExpect(status().isOk());

        // Validate the Contacts in the database
        List<Contacts> contactsList = contactsRepository.findAll();
        assertThat(contactsList).hasSize(databaseSizeBeforeUpdate);
        Contacts testContacts = contactsList.get(contactsList.size() - 1);
        assertThat(testContacts.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testContacts.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testContacts.getEmail()).isEqualTo(UPDATED_EMAIL);
    }

    @Test
    void patchNonExistingContacts() throws Exception {
        int databaseSizeBeforeUpdate = contactsRepository.findAll().size();
        contacts.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContactsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contacts.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(contacts))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contacts in the database
        List<Contacts> contactsList = contactsRepository.findAll();
        assertThat(contactsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchContacts() throws Exception {
        int databaseSizeBeforeUpdate = contactsRepository.findAll().size();
        contacts.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(contacts))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contacts in the database
        List<Contacts> contactsList = contactsRepository.findAll();
        assertThat(contactsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamContacts() throws Exception {
        int databaseSizeBeforeUpdate = contactsRepository.findAll().size();
        contacts.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContactsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(contacts)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Contacts in the database
        List<Contacts> contactsList = contactsRepository.findAll();
        assertThat(contactsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteContacts() throws Exception {
        // Initialize the database
        contactsRepository.save(contacts);

        int databaseSizeBeforeDelete = contactsRepository.findAll().size();

        // Delete the contacts
        restContactsMockMvc
            .perform(delete(ENTITY_API_URL_ID, contacts.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Contacts> contactsList = contactsRepository.findAll();
        assertThat(contactsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
