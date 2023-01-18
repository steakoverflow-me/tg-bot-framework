{:else {txts/main-menu {:else ["START" [nil]]}
        txts/dishes-list {:else ["DISHES:LIST" :admin [nil]]}}

 "START"       {:else            {:else [act/main-menu]}}

 "DISHES:LIST" {txts/dishes-add  {:else ["DISHES:ADD:CATEGORY" :admin]}
                nil              {"DISHES:VIEW" ["DISHES:VIEW" :admin []]}
                :else            {:else [act/dishes-list]}}
 "DISHES:VIEW" {txts/dishes-edit     {:else [act/dishes-edit "DISHES:EDIT" :admin []]}
                txts/dishes-activate {:else [act/dishes-activate "DISHES:LIST" :admin [nil]]}
                txts/dishes-disable  {:else [act/dishes-disable "DISHES:LIST" :admin [nil]]}
                :else            {:else [act/dishes-view]}}
 "DISHES:EDIT" {txts/dishes-edit}
 "DISHES:ADD:CATEGORY" {nil      {"DISHES:ADD:CATEGORY" ["DISHES:ADD:NAME" :admin [:dish-category]]}
                        :else    {:else [act/dishes-add-category]}}
 "DISHES:ADD:NAME" {:text        {:else ["DISHES:ADD:DESCRIPTION" :admin [:name]]}
                    :else        {:else [act/dishes-add-name]}}
 "DISHES:ADD:DESCRIPTION" {:text {:else ["DISHES:ADD:PICTURE" :admin [:description]]}
                           :else {:else [act/dishes-add-description]}}
 "DISHES:ADD:PICTURE" {:image    {:else ["DISHES:ADD:PRICE" :admin [:picture]]}
                       :else     {:else [act/dishes-add-picture]}}
 "DISHES:ADD:PRICE" {:number     {:else ["DISHES:ADD:APPROVE" :admin [:price]]}
                     :else       {:else [act/dishes-add-price]}}
 "DISHES:ADD:APPROVE" {txts/approve {:else [act/dishes-add-approved "DISHES:LIST" :admin [nil]]}
                       :else        {:else [act/dishes-add-approve]}}}

