package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Contacts;
import com.mycompany.myapp.repository.ContactsRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Contacts}.
 */
@RestController
@RequestMapping("/api")
public class ContactsResource {

    private final Logger log = LoggerFactory.getLogger(ContactsResource.class);

    private static final String ENTITY_NAME = "contacts";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ContactsRepository contactsRepository;

    public ContactsResource(ContactsRepository contactsRepository) {
        this.contactsRepository = contactsRepository;
    }

    /**
     * {@code POST  /contacts} : Create a new contacts.
     *
     * @param contacts the contacts to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new contacts, or with status {@code 400 (Bad Request)} if the contacts has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/contacts")
    public ResponseEntity<Contacts> createContacts(@RequestBody Contacts contacts) throws URISyntaxException {
        log.debug("REST request to save Contacts : {}", contacts);
        if (contacts.getId() != null) {
            throw new BadRequestAlertException("A new contacts cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Contacts result = contactsRepository.save(contacts);
        return ResponseEntity
            .created(new URI("/api/contacts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /contacts/:id} : Updates an existing contacts.
     *
     * @param id the id of the contacts to save.
     * @param contacts the contacts to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contacts,
     * or with status {@code 400 (Bad Request)} if the contacts is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contacts couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/contacts/{id}")
    public ResponseEntity<Contacts> updateContacts(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Contacts contacts
    ) throws URISyntaxException {
        log.debug("REST request to update Contacts : {}, {}", id, contacts);
        if (contacts.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contacts.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contactsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Contacts result = contactsRepository.save(contacts);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contacts.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /contacts/:id} : Partial updates given fields of an existing contacts, field will ignore if it is null
     *
     * @param id the id of the contacts to save.
     * @param contacts the contacts to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contacts,
     * or with status {@code 400 (Bad Request)} if the contacts is not valid,
     * or with status {@code 404 (Not Found)} if the contacts is not found,
     * or with status {@code 500 (Internal Server Error)} if the contacts couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/contacts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Contacts> partialUpdateContacts(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody Contacts contacts
    ) throws URISyntaxException {
        log.debug("REST request to partial update Contacts partially : {}, {}", id, contacts);
        if (contacts.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contacts.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contactsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Contacts> result = contactsRepository
            .findById(contacts.getId())
            .map(existingContacts -> {
                if (contacts.getFirstname() != null) {
                    existingContacts.setFirstname(contacts.getFirstname());
                }
                if (contacts.getLastname() != null) {
                    existingContacts.setLastname(contacts.getLastname());
                }
                if (contacts.getEmail() != null) {
                    existingContacts.setEmail(contacts.getEmail());
                }

                return existingContacts;
            })
            .map(contactsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contacts.getId())
        );
    }

    /**
     * {@code GET  /contacts} : get all the contacts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of contacts in body.
     */
    @GetMapping("/contacts")
    public ResponseEntity<List<Contacts>> getAllContacts(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Contacts");
        Page<Contacts> page = contactsRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /contacts/:id} : get the "id" contacts.
     *
     * @param id the id of the contacts to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the contacts, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/contacts/{id}")
    public ResponseEntity<Contacts> getContacts(@PathVariable String id) {
        log.debug("REST request to get Contacts : {}", id);
        Optional<Contacts> contacts = contactsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(contacts);
    }

    /**
     * {@code DELETE  /contacts/:id} : delete the "id" contacts.
     *
     * @param id the id of the contacts to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/contacts/{id}")
    public ResponseEntity<Void> deleteContacts(@PathVariable String id) {
        log.debug("REST request to delete Contacts : {}", id);
        contactsRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
