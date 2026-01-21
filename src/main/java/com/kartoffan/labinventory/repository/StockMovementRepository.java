package com.kartoffan.labinventory.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kartoffan.labinventory.model.Item;
import com.kartoffan.labinventory.model.StockMovement;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID>, JpaSpecificationExecutor<StockMovement> {

    /**
     * Calculates the current total quantity of a specific item within a specific
     * lab.
     * * @param itemId The unique identifier of the item
     * * @param labId  The unique identifier of the lab.
     *
     * @return The aggregated quantity, or 0 if no movements are found.
     */
    @Query("""
        SELECT COALESCE(SUM(
            CASE
                WHEN sm.type = 'IN' THEN sm.quantity
                WHEN sm.type = 'OUT' THEN sm.quantity
                WHEN sm.type = 'ADJUST' THEN sm.quantity
            END
        ), 0)
        FROM StockMovement sm
        WHERE sm.item.id = :itemId AND sm.lab.id = :labId
        """)
    int getCurrentQuantityByLab(UUID itemId, UUID labId);
    
    /**
     * Retrieves stock levels for all items currently or previously held in a
     * specific lab.
     * * @param labId The unique identifier of the lab.
     *
     * @return A list of Object arrays where [0] is the Item UUID and [1] is the
     *         Double quantity.
     */
    @Query("""
        SELECT sm.item.id, COALESCE(SUM(
            CASE
                WHEN sm.type = 'IN' THEN sm.quantity
                WHEN sm.type = 'OUT' THEN sm.quantity
                WHEN sm.type = 'ADJUST' THEN sm.quantity
            END
        ), 0)
        FROM StockMovement sm
        WHERE sm.lab.id = :labId
        GROUP BY sm.item.id
        """)
    List<Object[]> getAllItemQuantitiesByLab(UUID labId);

    /**
     * Identifies items that are at or below their specific 'lowStockThreshold'.
     * * @param labId The lab to check.
     *
     * @return List of Items requiring replenishment.
     */
    @Query("""
                SELECT i FROM Item i
                JOIN StockMovement sm ON sm.item.id = i.id
                WHERE sm.lab.id = :labId
                GROUP BY i.id
                HAVING COALESCE(SUM(
                    CASE
                        WHEN sm.type = 'IN' THEN sm.quantity
                        WHEN sm.type = 'OUT' THEN sm.quantity
                        WHEN sm.type = 'ADJUST' THEN sm.quantity
                    END
                ), 0) <= i.lowStockThreshold
                """)
    List<Item> findLowStockItemsByLab(UUID labId);

    /**
     * Identifies items that have reached zero or negative stock levels.
     * * @param labId The lab to check.
     *
     * @return List of Items that are currently unavailable.
     */
    @Query("""
        SELECT i FROM Item i
        JOIN StockMovement sm ON sm.item.id = i.id
        WHERE sm.lab.id = :labId
        GROUP BY i.id
        HAVING COALESCE(SUM(
            CASE
                WHEN sm.type = 'IN' THEN sm.quantity
                WHEN sm.type = 'OUT' THEN sm.quantity
                WHEN sm.type = 'ADJUST' THEN sm.quantity
            END
        ), 0) <= 0
        """)
    List<Item> findOutOfStockItemsByLab(UUID labId);
        
}
