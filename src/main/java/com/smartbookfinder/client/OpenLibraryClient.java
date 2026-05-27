package com.smartbookfinder.client;

import com.smartbookfinder.entity.SupportedLanguage;

public interface OpenLibraryClient {
    OpenLibrarySearchResponse search(String title, String author, SupportedLanguage language, int limit);
}
