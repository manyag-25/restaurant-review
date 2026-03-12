# Project Requirements Document (PRD)

## Product Name
**MealMeter**

## Product Overview
MealMeter is a restaurant feedback application that allows patrons to provide structured feedback about their dining experience directly at the point of sale. The application enables customers to rate key aspects of their visit and leave written comments that help restaurants understand customer satisfaction and identify areas for improvement.

The system collects feedback in three primary categories:

- Food quality
- Cleanliness
- Service

In addition to ratings, patrons may submit a short written review describing their experience. This information is recorded and made available to the restaurant for analysis and service improvement.

## Problem Statement
Restaurants rely heavily on customer satisfaction to maintain their reputation and attract repeat customers. However, many establishments struggle to collect structured feedback from patrons during or immediately after their dining experience.

Current feedback mechanisms such as online review platforms often suffer from several limitations:

- Feedback is delayed or forgotten after customers leave the restaurant
- Reviews are often unstructured and difficult to analyze
- Restaurants may not receive feedback until long after the dining experience
- Restaurants may not be able to distinguish genuine reviews from bad-faith actors leaving review bombs

This project aims to provide restaurants with a simple and immediate way to collect meaningful feedback from patrons directly at the point of sale.

## Target Users / Stakeholders
- Restaurant Patrons
- Restaurant Owners

## User Stories

| As a... | I want to... | So that I can... |
|---|---|---|
| Restaurant Patron | leave a restaurant rating | express satisfaction or dissatisfaction with my dining experience |
| Restaurant Patron | leave a restaurant review | provide targeted feedback on what I think should be improved |
| Restaurant Patron | submit feedback quickly | leave the restaurant relatively quickly after making payment |
| Restaurant Owner | tag restaurant reviews based on the written review | categorise feedback based on each area of improvement |
| Restaurant Owner | sort reviews based on rating | evaluate positive and negative feedback separately |
| Restaurant Owner | filter reviews based on criteria such as rating or tags | view targeted feedback in categories such as food, service, and cleanliness |
| Restaurant Owner | mark reviews as resolved or outstanding | indicate which complaints have been addressed while maintaining an archive of all reviews |
| Restaurant Owner | delete reviews that are useless | focus on actual reviews from patrons |

## Functional Requirements
- Restaurant Patrons should be able to leave a general rating
- Restaurant Patrons should be able to give a more specific rating for each service category
- Restaurant Patrons should be allowed to leave a written review
- Restaurant Owners should be able to tag reviews with custom tags for filtering
- Restaurant Owners should be able to view all patron reviews
- Restaurant Owners should be able to filter reviews by specified criteria
- Restaurant Owners should be able to mark reviews as resolved or outstanding
- Restaurant Owners should be able to delete reviews

## Non-Functional Requirements

### Performance
- The system should be able to store at least 100 user reviews
- The system should be able to sort and filter reviews within 2 seconds

### Usability
- The interface should be simple and easy to use

### Reliability
- The system should not lose more than 5 minutes of work in the event of an application crash

### Security
- Passwords should be stored securely
- The application should not have a server component