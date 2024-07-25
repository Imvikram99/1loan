package com.megaloans.lms.controller;


import com.megaloans.lms.dto.request.LoanCreationRequest;
import com.megaloans.lms.dto.request.RepaymentRequest;
import com.megaloans.lms.model.LoanDetails;
import com.megaloans.lms.model.LoanPayment;
import com.megaloans.lms.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanDetails> createLoan(@RequestBody LoanCreationRequest request) {
        LoanDetails loanDetails = loanService.createLoan(request);
        return ResponseEntity.ok(loanDetails);
    }

    @PostMapping("/repayments")
    public ResponseEntity<List<Boolean>> makeRepayment(@RequestBody RepaymentRequest request) {
        int threadCount = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        BlockingQueue<RepaymentRequest> queue = new LinkedBlockingQueue<>(1000);


        for (int i = 0; i < threadCount; i++) {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> queue.offer(request), executor);
            //this will create dead lock becasue txns waiting on each other so using queue to process one by one is better
            //CompletableFuture<LoanPayment> futuredd = CompletableFuture.supplyAsync(() -> loanService.makeRepayment(request), executor);
            futures.add(future);
        }
        processQueue(queue);


        List<Boolean> loanPayments = futures.stream()
                .map(CompletableFuture::join)  // Waits for each future to complete
                .collect(Collectors.toList());

        executor.shutdown();  // Shut down the executor after use
        return ResponseEntity.ok(loanPayments);
    }
    private void processQueue(BlockingQueue<RepaymentRequest> queue) {
        while (true) {
            try {
                // Take from the queue (blocking if necessary)
                RepaymentRequest request = queue.take();
                LoanPayment loanPayment = loanService.makeRepayment(request);
                if(queue.isEmpty()){
                    System.out.println("everthing processed");
                    break;
                }
                // Handle the processed loanPayment as needed, e.g., logging, storing in DB, etc.
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;  // Exit the loop if the thread is interrupted
            } catch (Exception e) {
                // Handle any exceptions that occurred during processing
                e.printStackTrace();
                break;
            }
        }
    }


    @GetMapping("/{loanId}")
    public ResponseEntity<LoanDetails> getLoanDetails(@PathVariable int loanId) {
        LoanDetails loanDetails = loanService.getLoanDetails(loanId);
        return ResponseEntity.ok(loanDetails);
    }
}
