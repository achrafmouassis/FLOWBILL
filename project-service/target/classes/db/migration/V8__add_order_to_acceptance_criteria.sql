-- V8__add_order_to_acceptance_criteria.sql

ALTER TABLE acceptance_criteria 
ADD COLUMN "order" INTEGER;

-- Initialize order for existing criteria
UPDATE acceptance_criteria ac
SET "order" = (
    SELECT count(*) 
    FROM acceptance_criteria ac2 
    WHERE ac2.task_id = ac.task_id 
      AND ac2.id <= ac.id
);

-- Make it non-null after init
ALTER TABLE acceptance_criteria 
ALTER COLUMN "order" SET NOT NULL;

-- Index for sorting performance
CREATE INDEX idx_acceptance_criteria_order 
ON acceptance_criteria(task_id, "order");
