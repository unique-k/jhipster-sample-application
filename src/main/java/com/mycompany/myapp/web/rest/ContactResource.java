package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Contact;
import com.mycompany.myapp.repository.ContactRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Contact}.
 */
@RestController
@RequestMapping("/api")
public class ContactResource {

    private final Logger log = LoggerFactory.getLogger(ContactResource.class);

    private static final String ENTITY_NAME = "contact";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ContactRepository contactRepository;

    public ContactResource(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * {@code POST  /contacts} : Create a new contact.
     *
     * @param contact the contact to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new contact, or with status {@code 400 (Bad Request)} if the contact has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/contacts")
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) throws URISyntaxException {
        log.debug("REST request to save Contact : {}", contact);
        if (contact.getId() != null) {
            throw new BadRequestAlertException("A new contact cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Contact result = contactRepository.save(contact);
        return ResponseEntity
            .created(new URI("/api/contacts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /contacts/:id} : Updates an existing contact.
     *
     * @param id the id of the contact to save.
     * @param contact the contact to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contact,
     * or with status {@code 400 (Bad Request)} if the contact is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contact couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/contacts/{id}")
    public ResponseEntity<Contact> updateContact(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Contact contact
    ) throws URISyntaxException {
        log.debug("REST request to update Contact : {}, {}", id, contact);
        if (contact.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contact.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contactRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Contact result = contactRepository.save(contact);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contact.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /contacts/:id} : Partial updates given fields of an existing contact, field will ignore if it is null
     *
     * @param id the id of the contact to save.
     * @param contact the contact to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contact,
     * or with status {@code 400 (Bad Request)} if the contact is not valid,
     * or with status {@code 404 (Not Found)} if the contact is not found,
     * or with status {@code 500 (Internal Server Error)} if the contact couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/contacts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Contact> partialUpdateContact(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Contact contact
    ) throws URISyntaxException {
        log.debug("REST request to partial update Contact partially : {}, {}", id, contact);
        if (contact.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contact.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contactRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Contact> result = contactRepository
            .findById(contact.getId())
            .map(existingContact -> {
                if (contact.getFirstname() != null) {
                    existingContact.setFirstname(contact.getFirstname());
                }
                if (contact.getLastname() != null) {
                    existingContact.setLastname(contact.getLastname());
                }
                if (contact.getEmail() != null) {
                    existingContact.setEmail(contact.getEmail());
                }

                return existingContact;
            })
            .map(contactRepository::save);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contact.getId()));
    }

    /**
     * {@code GET  /contacts} : get all the contacts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of contacts in body.
     */
    @GetMapping("/contacts")
    public ResponseEntity<List<Contact>> getAllContacts(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Contacts");
        Page<Contact> page = contactRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /contacts/:id} : get the "id" contact.
     *
     * @param id the id of the contact to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the contact, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/contacts/{id}")
    public ResponseEntity<Contact> getContact(@PathVariable String id) {
        log.debug("REST request to get Contact : {}", id);
        Optional<Contact> contact = contactRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(contact);
    }

    /**
     * {@code DELETE  /contacts/:id} : delete the "id" contact.
     *
     * @param id the id of the contact to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/contacts/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable String id) {
        log.debug("REST request to delete Contact : {}", id);
        contactRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
