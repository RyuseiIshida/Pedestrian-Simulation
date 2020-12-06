# -*- coding: utf-8 -*-
import os
import sys
import glob
import csv
import gensim
import pyLDAvis
import pyLDAvis.gensim

from gensim.models import CoherenceModel

class AnalysisTopic:
    def __init__(self, files_path, num_topics=3, analysis_data_name="group_size_split_corpus",
                 verification_data_name="step_split_corpus", verification_split="60"):
        self.files_path = files_path
        self.num_topics = num_topics
        self.analysis_data_name = analysis_data_name + ".txt"
        self.verification_data_name = verification_data_name + verification_split + ".txt"
        self.verification_split = verification_split
        self.dictionary = None
        self.corpus = None
        self.lda = None

    def create_topic(self):
        file = open(self.files_path + "/" + self.analysis_data_name, "r")
        data = []
        for line in file.readlines():
            data.append(line.rstrip().split(","))
        file.close()
        self.dictionary = gensim.corpora.Dictionary(data)
        # dictionary.filter_extremes(no_below=5, no_above=5)
        # create corpus
        self.corpus = [self.dictionary.doc2bow(words) for words in data]
        # LDAモデルの構築
        self.lda = gensim.models.ldamodel.LdaModel(corpus=self.corpus,
                                                   num_topics=self.num_topics,
                                                   id2word=self.dictionary,
                                                   random_state=1)
        # # coherence = CoherenceModel(model=self.lda, corpus=self.corpus, coherence='u_mass').get_coherence()
        # coherence = CoherenceModel(model=self.lda, texts=data, dictionary=self.dictionary,
        #                            coherence='c_v').get_coherence()
        # perplexity = np.exp2(-self.lda.log_perplexity(self.corpus))
        # # print(f"num_topics = {self.num_topics},coherence = {coherence},perplexity = {perplexity}")
        # print(f"{self.num_topics},{coherence},{perplexity}")

    def _mkdir_out(self):
        out_file_path = self.files_path + "/topic_k" + str(self.num_topics)
        if not os.path.exists(out_file_path):
            os.mkdir(out_file_path)

    def write_topic(self):
        self._mkdir_out()
        out_file_path = self.files_path + "/topic_k" + str(self.num_topics)
        for topic_i in range(self.num_topics):
            file = open(out_file_path + "/group_topic" + str(topic_i + 1) + ".txt", "w")
            for topic_word in self.lda.show_topic(topic_i):
                file.write(topic_word[0] + "," + str(topic_word[1]))
                file.write("\n")
            file.close()

    def write_fit_topic(self):
        self._mkdir_out()
        read_file = open(self.files_path + "/" + self.verification_data_name, "r")
        out_file_path = self.files_path + "/topic_k" + str(self.num_topics)
        out_file = open(out_file_path + "/fit_topic" + self.verification_split + ".csv", "w")
        writer = csv.writer(out_file)
        csv_label = ["step"] + ["topic{}".format(topic_number + 1) for topic_number in range(self.num_topics)]
        writer.writerow(csv_label)
        for i, line in enumerate(csv.reader(read_file)):
            data = [line]
            corpus = [self.dictionary.doc2bow(words) for words in data]
            write_topic = [0] * (self.num_topics + 1)
            #  num step
            write_topic[0] = i + 1
            for topics_per_document in self.lda[corpus]:
                for topic in topics_per_document:
                    write_topic[topic[0] + 1] = topic[1]
            writer.writerow(write_topic)
        read_file.close()
        out_file.close()

    def write_LDAvis(self):
        self._mkdir_out()
        out_file_path = self.files_path + "/topic_k" + str(self.num_topics)
        vis_pcoa = pyLDAvis.gensim.prepare(self.lda, self.corpus, self.dictionary, mds='pcoa', sort_topics=False)
        # vis_mds = pyLDAvis.gensim.prepare(self.lda, self.corpus, self.dictionary, mds='mmds', sort_topics=False)
        # vis_tsne = pyLDAvis.gensim.prepare(self.lda, self.corpus, self.dictionary, mds='tsne', sort_topics=False)
        pyLDAvis.save_html(vis_pcoa, out_file_path + '/pyldavis_output_pcoa.html')
        # pyLDAvis.save_html(vis_mds, out_file_path + '/pyldavis_output_mmds.html')
        # pyLDAvis.save_html(vis_tsne, out_file_path + '/pyldavis_output_tsne.html')


if __name__ == '__main__':
    file_dir = sys.argv[1]
    for i in range(3, 15+1):
        analysis_topic = AnalysisTopic(file_dir, num_topics=i, analysis_data_name="group_size_split_corpus",
        verification_data_name="step_split_corpus", verification_split="10")
        analysis_topic.create_topic()
        analysis_topic.write_topic()
        analysis_topic.write_fit_topic()
    #     analysis_topic.write_LDAvis()
    print("save topic data")
