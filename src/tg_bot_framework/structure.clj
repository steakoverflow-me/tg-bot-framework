{:else {txts/main-menu {:else ["START" [nil]]}}

      "START"       {txts/dishes-list {:else ["DISHES:LIST" :admin [nil]]}
                     :else            {:else [act/main-menu]}}

      "DISHES:LIST" {txts/dishes-add  {:else ["DISHES:ADD:NAME" :admin]}
                     nil              {"DISHES:VIEW" ["DISHES:VIEW" :admin []]}
                     :else            {:else [act/dishes-list]}}
      "DISHES:VIEW" {:else            {:else [act/dishes-view]}}
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
